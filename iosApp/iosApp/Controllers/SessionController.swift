import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class SessionController : UIViewController, SessionDetailsView {
    @IBOutlet weak var startButton: UIButton!
    @IBOutlet weak var videoBox: YTPlayerView!

    @IBOutlet weak var roomLabel: UILabel!
    @IBOutlet weak var slidesLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var tagsLabel: UILabel!

    @IBOutlet weak var voteDown: UIButton!
    @IBOutlet weak var voteSoso: UIButton!
    @IBOutlet weak var voteUp: UIButton!

    @IBOutlet weak var speakerFirst: TouchableLabel!
    @IBOutlet weak var speakerSecond: TouchableLabel!

    private var presenter: SessionDetailsPresenter!
//    var session: Session!

    override func viewDidLoad() {
        super.viewDidLoad()

        startButton.setImage(UIImage(named: "favoriteEmpty.png"), for: .normal)
        startButton.setImage(UIImage(named: "favoriteSelected.png"), for: .selected)
        startButton.isSelected = session.isFavorite

        videoBox.load(withVideoId: "wZZ7oFKsKzY")

//        func setupSpeaker(label: TouchableLabel, speaker: Speaker) {
//            label.font = UIFont.headerTextRegular
//            label.text = speaker.fullName
//
//            label.onTouchUp = {
//                let speakerBoard = UIStoryboard(name: "Main", bundle: nil)
//                let speakerController = speakerBoard.instantiateViewController(withIdentifier: "Speaker") as! SpeakerController
//
//                speakerController.speaker = speaker
//                self.navigationController?.pushViewController(speakerController, animated: true)
//            }
//        }

        let speakers = session.speakers
        setupSpeaker(label: speakerFirst, speaker: speakers[0])
        if (speakers.count > 1) {
            speakerSecond.isHidden = false
            setupSpeaker(label: speakerSecond, speaker: speakers[1])
        } else {
            speakerSecond.isHidden = true
        }

        presenter = SessionDetailsPresenter(view: self, session: session, service: AppDelegate.service)

        let titleText = session.title
        if (titleText.count > 30) {
            titleLabel.font = UIFont.headerScreenSmall
        } else {
            titleLabel.font = UIFont.headerScreen
        }

        titleLabel.text = titleText.uppercased()

        descriptionLabel.attributedText = LetterSpacedText(text: session.descriptionText, spacing: 0.52)
        roomLabel.text = session.room.name

        tagsLabel.text = session.tags.joined(separator: " ")

        updateRating(rating: session.rating)
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func voteUpTouch(_ sender: Any) {
        presenter.onRatingButtonClicked(clicked: RatingData.good)
    }

    @IBAction func voteSosoTouch(_ sender: Any) {
        presenter.onRatingButtonClicked(clicked: RatingData.ok)
    }

    @IBAction func voteDownTouch(_ sender: Any) {
        presenter.onRatingButtonClicked(clicked: RatingData.bad)
    }

    @IBAction func favoriteClick(_ sender: Any) {
        startButton.isSelected = !startButton.isSelected
        presenter.onFavoriteButtonClicked()
    }

    func updateRating(rating: RatingData?) {
        voteDown.isSelected = rating == RatingData.bad
        voteUp.isSelected = rating == RatingData.good
        voteSoso.isSelected = rating == RatingData.ok
    }

    func setRatingClickable(clickable: Bool) {
        voteDown.isEnabled = clickable
        voteUp.isEnabled = clickable
        voteSoso.isEnabled = clickable
    }

    func updateFavorite(isFavorite: Bool) {
        startButton.isSelected = isFavorite
    }
}
