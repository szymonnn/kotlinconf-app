import UIKit
import KotlinConfAPI
import Nuke

class TweetPost : UICollectionViewCell {
    @IBOutlet weak var author: UILabel!
    @IBOutlet weak var account: UILabel!
    @IBOutlet weak var time: UILabel!
    @IBOutlet weak var text: UILabel!

    @IBOutlet weak var media: UIImageView!
    @IBOutlet weak var avatar: UIImageView!

    var post: FeedPost! {
        didSet {
            let user = post.user

            author.text = user.name
            account.text = "@" + user.screen_name

            time.text = post.displayDate()
            text.text = post.text

            Nuke.loadImage(with: URL(string: user.profile_image_url_https)!, into: avatar)
        }
    }
}
