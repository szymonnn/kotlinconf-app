import Foundation
import UIKit
import KotlinConfAPI

class HomeController : UIViewController, UICollectionViewDataSource, UIGestureRecognizerDelegate {
    @IBOutlet weak var videosView: UICollectionView!
    @IBOutlet weak var feedView: UICollectionView!
    @IBOutlet weak var upcomingFavorites: UIStackView!
    @IBOutlet weak var dontMissLabel: UILabel!

    private var liveSessions: [SessionCard] = []
    private var feedData: [FeedPost] = []
    private var upcoming: [SessionCardView] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        videosView.dataSource = self
        videosView.delegate = self

        feedView.dataSource = self
        feedView.delegate = self

        Conference.liveSessions.watch { cards in
            self.onLiveSessions(sessions: cards as! [SessionCard])
        }

        Conference.upcomingFavorites.watch { cards in
            self.onUpcomingFavorites(sessions: cards as! [SessionCard])
        }

        Conference.feed.watch { feed in
            self.onFeedData(feed: feed)
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
        navigationController!.interactivePopGestureRecognizer!.delegate = self
    }

    private func onLiveSessions(sessions: [SessionCard]) {
        liveSessions = sessions
        videosView.reloadData()
    }

    private func onUpcomingFavorites(sessions: [SessionCard]) {
        dontMissLabel.isHidden = sessions.count == 0
        for card in upcoming {
            upcomingFavorites.removeArrangedSubview(card)
            card.cleanup()
        }

        upcoming = sessions.map { card in
            let view = SessionCardView()
            view.card = card
            view.setupDarkMode()

            view.onTouch = {
                self.showScreen(name: "Session", config: { controller in
                    (controller as! SessionController).card = card
                })
            }

            upcomingFavorites.addArrangedSubview(view)
            upcomingFavorites.setCustomSpacing(5.0, after: view)
            return view
        }
    }

    private func onFeedData(feed: FeedData?) {
        if (feed == nil) {
            return
        }

        feedData = feed!.statuses
        feedView.reloadData()
    }

    @IBAction func showPartner(_ sender: UIButton, forEvent event: UIEvent) {
        showScreen(name: "Partner") { controller in
            (controller as! PartnerController).partnerId = sender.tag
        }
    }

    func showScreen(name: String, config: (UIViewController) -> Void = { controller -> Void in return }) {
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: name)
        config(controller)
        self.navigationController?.pushViewController(controller, animated: true)
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        switch collectionView {
        case videosView:
            return liveSessions.count
        case feedView:
            return feedData.count
        default:
            return 0
        }
    }

    func collectionView(
        _ collectionView: UICollectionView,
        cellForItemAt indexPath: IndexPath
    ) -> UICollectionViewCell {
        switch collectionView {
        case feedView:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "TweetPost", for: indexPath) as! TweetPost
            item.post = feedData[indexPath.row]
            return item
        default:
            let item = collectionView.dequeueReusableCell(
                withReuseIdentifier: "LiveVideo", for: indexPath
            ) as! LiveVideo
            item.card = liveSessions[indexPath.row]
            return item
        }
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        switch collectionView {
        case videosView:
            let card = liveSessions[indexPath.row]
            showScreen(name: "Session") { controller in
                (controller as! SessionController).card = card
            }
        default:
            return
        }
    }
}

extension HomeController : UIScrollViewDelegate, UICollectionViewDelegate {
    func scrollViewWillEndDragging(
        _ scrollView: UIScrollView,
        withVelocity velocity: CGPoint,
        targetContentOffset: UnsafeMutablePointer<CGPoint>
    ) {
        let view: UICollectionView = {
            switch scrollView {
            default:
                return self.videosView
            }
        }()

        let layout = view.collectionViewLayout as! UICollectionViewFlowLayout
        var offset = targetContentOffset.pointee
        let cellWidth = layout.itemSize.width + layout.minimumLineSpacing

        let leftInset = scrollView.contentInset.left
        let index = round((offset.x + leftInset) / cellWidth)

        offset.x = index * cellWidth - leftInset

        targetContentOffset.pointee = offset
    }
}
