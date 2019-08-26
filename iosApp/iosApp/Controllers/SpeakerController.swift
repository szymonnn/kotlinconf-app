import Foundation
import UIKit
import KotlinConfAPI

class SpeakerController : UIViewController, SpeakerView {
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var photoView: UIImageView!
    @IBOutlet weak var positionLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var talksContainer: UIStackView!

    private var presenter: SpeakerPresenter! {
        return SpeakerPresenter(view: self)
    }

    var speaker: SpeakerData!

    override func viewDidLoad() {
        nameLabel.text = speaker.fullName.uppercased()

        if let profilePicture = speaker.profilePicture {
            do {
                let pictureUrl = URL(string: profilePicture)
                photoView.image = UIImage(data: try Data(contentsOf: pictureUrl!))
            } catch {
                print("Failed to load image: " + profilePicture)
            }
        }

        descriptionLabel.attributedText = LetterSpacedText(text: speaker.bio, spacing: 0.52)
        positionLabel.text = speaker.tagLine
    }

    override func viewWillAppear(_ animated: Bool) {
        let sessions = presenter.sessionsForSpeaker(id: speaker.id)

        for card in sessions {
            let view = SessionCardView()
            view.card = card
            setupCard(view)
            talksContainer.addArrangedSubview(view)
            talksContainer.setCustomSpacing(5.0, after: view)
        }
    }

    private func setupCard(_ card: SessionCardView) {
        // handle events
    }

    override func viewWillDisappear(_ animated: Bool) {
        for item in talksContainer.subviews {
            let cardView = item as! SessionCardView
            cardView.cleanup()
        }
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
