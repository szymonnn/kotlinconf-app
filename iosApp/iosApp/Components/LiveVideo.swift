import Foundation
import UIKit
import youtube_ios_player_helper
import KotlinConfAPI

class LiveVideo : UICollectionViewCell {
    @IBOutlet weak var sessionTitle: UILabel!
    @IBOutlet weak var speaker: UILabel!
    @IBOutlet weak var video: YTPlayerView!
    @IBOutlet weak var location: UILabel!
    @IBOutlet weak var favoriteButton: UIButton!

    var card: SessionCard! {
        didSet {
            sessionTitle.text = card.session.title
            speaker.text = card.speakers.map { (speaker) -> String in
                speaker.fullName
            }.joined(separator: ", ")

            location.text = card.location.name
            video.load(withVideoId: "YbF8Q8LxAJs")
        }
    }
}
