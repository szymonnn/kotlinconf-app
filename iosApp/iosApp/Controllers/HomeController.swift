import Foundation
import UIKit
import KotlinConfAPI

class HomeController : UIViewController, UICollectionViewDataSource, HomeView, UIGestureRecognizerDelegate {
    @IBOutlet weak var videosView: UICollectionView!

    private var liveSessions: [SessionCard] = []
    private var speakers: [SpeakerData] = []
    private var partners: [PartnerData] = []

    private var presenter: HomePresenter {
        return HomePresenter(view: self)
    }

    override func viewDidLoad() {
        videosView.dataSource = self
        videosView.delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
    }

    func onLiveSessions(sessions: [SessionCard]) {
        liveSessions = sessions
        videosView.reloadData()
    }

    func onDataReceive(data: SessionizeData) {
        speakers = data.speakers
        partners = data.partners
    }

    func showScreen(name: String, config: (UIViewController) -> Void = { controller -> Void in return }) {
        navigationController!.interactivePopGestureRecognizer!.isEnabled = true
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: name)
        config(controller)
        self.navigationController?.pushViewController(controller, animated: true)
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        switch collectionView {
        case videosView:
            return liveSessions.count
//        case speakersView:
//            return speakers.count
//        case partnersView:
//            return partners.count
        default:
            return 0
        }
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        switch collectionView {
        case videosView:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "LiveVideo", for: indexPath) as! LiveVideo
            item.card = liveSessions[indexPath.row]
            return item
//        case speakersView:
//            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "SpeakerCard", for: indexPath) as! SpeakerCard
//            item.speaker = speakers[indexPath.row]
//            return item
        default:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "PartnerCard", for: indexPath) as! PartnerCard
            item.partner = partners[indexPath.row]
            return item
        }
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        switch collectionView {
        case videosView:
//            let session = liveSessions[indexPath.row]
            showScreen(name: "Session", config: { controller in
//                (controller as! SessionController).session = session
            })
//        case speakersView:
//            let speaker = speakers[indexPath.row]
//
//            showScreen(name: "Speaker",config: { controller in
//                (controller as! SpeakerController).speaker = speaker
//            })
        default:
            return
        }
    }
}

extension HomeController : UIScrollViewDelegate, UICollectionViewDelegate {
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        let view: UICollectionView = {
            switch scrollView {
            default:
//            case self.videosView:
                return self.videosView
//            case self.speakersView:
//                return self.speakersView
//            default:
//                return self.partnersView
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

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        navigationController!.interactivePopGestureRecognizer!.delegate = self
        navigationController!.interactivePopGestureRecognizer!.isEnabled = false
    }
}
