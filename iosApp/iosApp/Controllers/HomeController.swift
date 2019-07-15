import Foundation
import UIKit
import KotlinConfAPI

class HomeController : UIViewController, UICollectionViewDataSource, HomeView, UIGestureRecognizerDelegate {
    @IBOutlet weak var videosView: UICollectionView!
    @IBOutlet weak var speakersView: UICollectionView!
    @IBOutlet weak var partnersView: UICollectionView!

    @IBOutlet weak var showSpeakers: TouchableLabel!
    @IBOutlet weak var showPartners: TouchableLabel!
    @IBOutlet weak var showFaq: TouchableLabel!

    private var liveSessions: [Session] = []
    private var speakers: [Speaker] = []
    private var partners: [Partner] = []

    private var presenter: HomePresenter {
        return HomePresenter(view: self, service: AppDelegate.service)
    }

    override func viewDidLoad() {
        navigationController?.isNavigationBarHidden = true

        videosView.dataSource = self
        videosView.delegate = self

        speakersView.dataSource = self
        speakersView.delegate = self

        partnersView.dataSource = self
        partnersView.delegate = self

        presenter.onCreate()

        showSpeakers.onTouchUp = {
            self.showScreen(name: "Speakers")
        }

        showPartners.onTouchUp = {
            self.showScreen(name: "Partners")
        }

        showFaq.onTouchUp = {
            self.showScreen(name: "FAQ")
        }

    }

    func showScreen(name: String, config: (UIViewController) -> Void = { controller -> Void in return }) {
        navigationController!.interactivePopGestureRecognizer!.isEnabled = true
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: name)
        config(controller)
        self.navigationController?.pushViewController(controller, animated: true)
    }

    func onLiveSessions(sessions: [Session]) {
        self.liveSessions = sessions
        videosView.reloadData()
    }

    func onSpeakers(speakers: [Speaker]) {
        self.speakers = speakers
        speakersView.reloadData()
    }

    func onPartners(partners: [Partner]) {
        self.partners = partners
        partnersView.reloadData()
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        switch collectionView {
        case videosView:
            return liveSessions.count
        case speakersView:
            return speakers.count
        case partnersView:
            return partners.count
        default:
            return 0
        }
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        switch collectionView {
        case videosView:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "LiveVideo", for: indexPath) as! LiveVideo
            item.session = liveSessions[indexPath.row]
            return item
        case speakersView:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "SpeakerCard", for: indexPath) as! SpeakerCard
            item.speaker = speakers[indexPath.row]
            return item
        default:
            let item = collectionView.dequeueReusableCell(withReuseIdentifier: "PartnerCard", for: indexPath) as! PartnerCard
            item.partner = partners[indexPath.row]
            return item
        }
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        switch collectionView {
        case videosView:
            let session = liveSessions[indexPath.row]
            showScreen(name: "Session", config: { controller in
                (controller as! SessionController).session = session
            })
        case speakersView:
            let speaker = speakers[indexPath.row]
            print("Select speaker: " + speaker.fullName)
            showScreen(name: "Speaker",config: { controller in
                (controller as! SpeakerController).speaker = speaker
            })
        default:
            return
        }
    }
}

extension HomeController : UIScrollViewDelegate, UICollectionViewDelegate {
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        let view: UICollectionView = {
            switch scrollView {
            case self.videosView:
                return self.videosView
            case self.speakersView:
                return self.speakersView
            default:
                return self.partnersView
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
