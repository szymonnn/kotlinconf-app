import Foundation
import UIKit
import KotlinConfAPI

class SessionCardView : UIView {
    @IBOutlet var mainView: UIView!

    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var speakers: UILabel!

    @IBOutlet weak var liveIcon: UIImageView!
    @IBOutlet weak var liveLabel: UILabel!

    @IBOutlet weak var location: UILabel!

    @IBOutlet weak var voteButton: UIButton!
    @IBOutlet weak var favoriteButton: UIButton!

    @IBOutlet weak var voteBar: UIView!

    var card: SessionCard! {
        didSet {
            title.text = card.session.title

            speakers.text = card.speakers.map({ (speaker) -> String in
                speaker.fullName
            }).joined(separator: ", ")

            liveIcon.isHidden = !card.isLive
            liveLabel.isHidden = !card.isLive
            voteBar.isHidden = true
        }
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        configure()
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        configure()
    }

    @IBAction func voteTouch(_ sender: Any) {
        UIView.transition(
            with: voteBar,
            duration: 0.3,
            options: [.transitionCrossDissolve],
            animations: {
                self.voteBar.isHidden = false
            },
            completion: nil
        )
    }

    @IBAction func favoriteTouch(_ sender: Any) {
    }

    private func configure() {
        Bundle.main.loadNibNamed("SessionCardView", owner: self, options: nil)
        addSubview(mainView)
        mainView.frame = self.bounds
        voteBar.isHidden = true

        voteBar.layer.shadowColor = UIColor.black.cgColor
        voteBar.layer.shadowOffset = CGSize(width: 0, height: 0.5)
        voteBar.layer.shadowOpacity = 0.15;
    }
}
