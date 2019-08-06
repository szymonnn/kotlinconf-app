import Foundation
import UIKit
import KotlinConfAPI

class SpeakerController : UIViewController {
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var photoView: UIImageView!
    @IBOutlet weak var positionLabel: UILabel!
    @IBOutlet weak var companyLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!

//    var speaker: Speaker!

    override func viewDidLoad() {
//        nameLabel.text = speaker.fullName.uppercased()
//
//        if let profilePicture = speaker.profilePicture {
//            do {
//                let pictureUrl = URL(string: profilePicture)
//                photoView.image = UIImage(data: try Data(contentsOf: pictureUrl!))
//            } catch {
//                print("Failed to load image: " + profilePicture)
//            }
//        }
//
//        descriptionLabel.attributedText = LetterSpacedText(text: speaker.bio, spacing: 0.52)

        companyLabel.text = "FILL ME IN SESSIONIZE"
        positionLabel.text = "FILL ME IN SESSIONIZE"

    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
