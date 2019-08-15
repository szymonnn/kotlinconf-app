import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class SessionController : UIViewController, SessionView {
    @IBOutlet weak var speaker1: UIButton!
    @IBOutlet weak var speaker2: UIButton!
    @IBOutlet weak var video: YTPlayerView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var locationLabel: UIButton!

    private var presenter: SessionPresenter! {
        return SessionPresenter(view: self)
    }

    private var ratingObserver: Observable<AnyObject>? = nil
    private var favoriteObserver: Observable<AnyObject>? = nil
    private var liveObserver: Observable<AnyObject>? = nil

    var card: SessionCard!
    private var borders: [CALayer]!

    override func viewWillAppear(_ animated: Bool) {
        let session = card.session

        // Description
        descriptionLabel.text = session.descriptionText

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
            button!.titleLabel!.sizeToFit()
            let width = button!.titleLabel!.bounds.size.width
            return button!.layer.addBorders(width + 24)
        })
    }

    override func viewDidDisappear(_ animated: Bool) {
        borders.forEach({ layer in
            layer.removeFromSuperlayer()
        })
        releaseObservers()
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    private func liveChange(_ isLive: Bool) {
    }

    private func ratingChange(_ rating: RatingData?) {
    }

    private func favoriteChange(_ isFavorite: Bool) {
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
    private func releaseObservers() {
        ratingObserver?.close()
        favoriteObserver?.close()
        liveObserver?.close()
    }
}
