import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class SessionController : UIViewController, SessionView, UIScrollViewDelegate {
    @IBOutlet weak var speaker1: UIButton!
    @IBOutlet weak var speaker2: UIButton!
    @IBOutlet weak var video: YTPlayerView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var locationLabel: UIButton!
    @IBOutlet weak var voteBar: UIView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var speakers: UIStackView!

    @IBOutlet weak var voteButton: UIButton!
    @IBOutlet weak var favoriteButton: UIButton!
    @IBOutlet weak var voteUp: UIButton!
    @IBOutlet weak var voteOk: UIButton!
    @IBOutlet weak var voteDown: UIButton!

    private var presenter: SessionPresenter! {
        return SessionPresenter(view: self)
    }

    private var ratingObserver: Observable<AnyObject>? = nil
    private var favoriteObserver: Observable<AnyObject>? = nil
    private var liveObserver: Observable<AnyObject>? = nil

    var card: SessionCard!
    private var borders: [CALayer]!

    override func viewDidLoad() {
        super.viewDidLoad()
        scrollView.delegate = self
    }

    @objc func onTouch(sender:UIGestureRecognizer) {
        voteBar.isHidden = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        let session = card.session
        voteBar.isHidden = true

        // Title
        titleLabel.text = session.title.uppercased()

        // Description
        let style = NSMutableParagraphStyle()
        style.minimumLineHeight = 24.0

        descriptionLabel.text = session.descriptionText
        descriptionLabel.attributedText = NSAttributedString(
            string: session.descriptionText,
            attributes: [NSAttributedString.Key.paragraphStyle : style]
        )

        // Speakers
        let firstSpeaker = card.speakers[0]
        speaker1.setTitle(firstSpeaker.fullName, for: .normal)
        if (card.speakers.count > 1) {
            speaker2.isHidden = false
            let secondSpeaker = card.speakers[1]
            speaker2.setTitle(secondSpeaker.fullName, for: .normal)
        } else {
            speaker2.isHidden = true
        }

        // Time
        timeLabel.text = card.time

        liveObserver = card.isLive.onChange(block: { isLive in
            self.liveChange(isLive!.boolValue)
        })

        // Location
        locationLabel.setTitle(card.location.name, for: .normal)

        // Favorite
        favoriteObserver = card.isFavorite.onChange(block: { isFavorite in
            self.favoriteChange(isFavorite!.boolValue)
        })

        // Rating
        ratingObserver = card.ratingData.onChange(block: { rating in
            self.ratingChange(rating)
        })

        // button borders
        borders = [speaker1, speaker2, locationLabel].map({ button in
            let label = button!.titleLabel
            label?.sizeToFit()
            let xOffset: CGFloat = (button?.imageView?.image != nil) ? 33.0 : 12.0
            return label!.layer.addBorders(label!.bounds.size, xOffset, 8)
        })
        speakers.sizeToFit()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        borders.forEach({ layer in
            layer.removeFromSuperlayer()
        })
        releaseObservers()
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func voteTouch(_ sender: Any) {
        voteBar.isHidden = false
    }

    @IBAction func favoriteTouch(_ sender: Any) {
        Conference.markFavorite(sessionId: card.session.id)
    }

    @IBAction func voteUpTouch(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: .good)
    }
    @IBAction func voteOkTouch(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: .ok)
    }
    @IBAction func voteDownTouch(_ sender: Any) {
        Conference.vote(sessionId: card.session.id, rating: .bad)
    }

    private func liveChange(_ isLive: Bool) {
        video.isHidden = !isLive
    }

    private func ratingChange(_ rating: RatingData?) {
        voteUp.isSelected = rating == .good
        voteOk.isSelected = rating == .ok
        voteDown.isSelected = rating == .bad

        let image: UIImage = {
            switch rating {
            case RatingData.good: return UIImage(named: "voteGoodWhite")!
            case RatingData.ok: return UIImage(named: "voteOkWhite")!
            case RatingData.bad: return UIImage(named: "voteBadWhite")!
            default: return UIImage(named: "voteGoodLight")!
            }
        }()

        voteButton.setImage(image, for: .normal)
        voteBar.isHidden = true
    }

    private func favoriteChange(_ isFavorite: Bool) {
        favoriteButton.isSelected = isFavorite
    }

//    @IBAction func favoriteClick(_ sender: Any) {
//        startButton.isSelected = !startButton.isSelected
//        presenter.favoriteTouch()
//    }
//
//    private func sendRating(rating: RatingData) {
//        setRatingClickable(clickable: false)
//        presenter.voteTouch(rating: rating)
//    }
//
//    private func setRatingClickable(clickable: Bool) {
//        voteDown.isEnabled = clickable
//        voteUp.isEnabled = clickable
//        voteSoso.isEnabled = clickable
//    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        voteBar.isHidden = true
    }

    private func releaseObservers() {
        ratingObserver?.close()
        favoriteObserver?.close()
        liveObserver?.close()
    }
}
