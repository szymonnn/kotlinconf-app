import Foundation
import UIKit
import KotlinConfAPI

class SpeakerCard : UICollectionViewCell {
    @IBOutlet weak var speakerPhoto: UIImageView!
    @IBOutlet weak var speakerName: UILabel!
    @IBOutlet weak var speakerDescription: UILabel!

    var speaker: Speaker! {
        didSet {
            speakerName.text = speaker.fullName
            speakerDescription.text = "FILL ME \nFILL ME"

            if let profilePicture = speaker.profilePicture {

                do {
                    let pictureUrl = URL(string: profilePicture)
                    speakerPhoto.image = UIImage(data: try Data(contentsOf: pictureUrl!))
                } catch {
                    print("Failed to load image: " + profilePicture)
                }
            }
        }
    }
}

class PartnerCard : UICollectionViewCell {
    @IBOutlet weak var partnerPhoto: UIImageView!
    @IBOutlet weak var partnerName: UILabel!
    @IBOutlet weak var partnerDescription: UILabel!

    var partner: Any! {
        didSet {
        }
    }
}
