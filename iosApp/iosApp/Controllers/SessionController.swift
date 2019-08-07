import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class SessionController : UIViewController, SessionView {
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

    private var presenter: SessionPresenter! {
        return SessionPresenter(view: self)
    }

    var session: SessionData!

    override func viewDidLoad() {
        super.viewDidLoad()

        startButton.setImage(UIImage(named: "favoriteEmpty.png"), for: .normal)
        startButton.setImage(UIImage(named: "favoriteSelected.png"), for: .selected)


        videoBox.load(withVideoId: "wZZ7oFKsKzY")

    }

    func onSession(session: SessionData, speakers: [SpeakerData], isFavorite: Bool, rating: RatingData?) {
        startButton.isSelected = isFavorite
        onVoteChange(rating: rating)


        func setupSpeaker(label: TouchableLabel, speaker: SpeakerData) {
            label.font = UIFont.headerTextRegular
            label.text = speaker.fullName

            label.onTouchUp = {
                let speakerBoard = UIStoryboard(name: "Main", bundle: nil)
                let speakerController = speakerBoard.instantiateViewController(withIdentifier: "Speaker") as! SpeakerController

                speakerController.speaker = speaker
                self.navigationController?.pushViewController(speakerController, animated: true)
            }
        }

//        let speakers = session.speakers
//        setupSpeaker(label: speakerFirst, speaker: speakers[0])
//        if (speakers.count > 1) {
//            speakerSecond.isHidden = false
//            setupSpeaker(label: speakerSecond, speaker: speakers[1])
//        } else {
//            speakerSecond.isHidden = true
//        }

        let titleText = session.title
        if (titleText.count > 30) {
            titleLabel.font = UIFont.headerScreenSmall
        } else {
            titleLabel.font = UIFont.headerScreen
        }

        titleLabel.text = titleText.uppercased()

        descriptionLabel.attributedText = LetterSpacedText(text: session.descriptionText, spacing: 0.52)
//        roomLabel.text = session.room.name
//        tagsLabel.text = session.tags.joined(separator: " ")
    }

    func onVoteChange(rating: RatingData?) {
        voteDown.isSelected = rating == RatingData.bad
        voteUp.isSelected = rating == RatingData.good
        voteSoso.isSelected = rating == RatingData.ok

        setRatingClickable(clickable: true)
    }

    func onUpdateFavorite(isFavorite: Bool) {
        startButton.isSelected = isFavorite
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func voteUpTouch(_ sender: Any) {
        sendRating(rating: RatingData.good)
    }

    @IBAction func voteSosoTouch(_ sender: Any) {
        sendRating(rating: RatingData.ok)
    }

    @IBAction func voteDownTouch(_ sender: Any) {
        sendRating(rating: RatingData.bad)
    }

    @IBAction func favoriteClick(_ sender: Any) {
        startButton.isSelected = !startButton.isSelected
        presenter.favoriteTouch()
    }


    private func sendRating(rating: RatingData) {
        setRatingClickable(clickable: false)
        presenter.voteTouch(rating: rating)
    }

    private func setRatingClickable(clickable: Bool) {
        voteDown.isEnabled = clickable
        voteUp.isEnabled = clickable
        voteSoso.isEnabled = clickable
    }
}
