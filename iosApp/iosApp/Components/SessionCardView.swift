import Foundation
import UIKit
import KotlinConfAPI

class SessionCardView : UIView, Baloon {
    @IBOutlet var mainView: UIView!

    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var speakers: UILabel!

    @IBOutlet weak var liveIcon: UIImageView!
    @IBOutlet weak var liveLabel: UILabel!

    @IBOutlet weak var location: UILabel!

    @IBOutlet weak var voteButton: UIButton!
    @IBOutlet weak var favoriteButton: UIButton!

    @IBOutlet weak var voteUp: UIButton!
    @IBOutlet weak var voteOk: UIButton!
    @IBOutlet weak var voteDown: UIButton!

    @IBOutlet weak var voteBar: UIView!

    var container: BaloonContainer? = nil
    var favoriteTouch: () -> Void = { }
    var voteTouch: (RatingData?) -> Void = { _ in }

    private var liveObservable: Observable<AnyObject>? = nil
    private var ratingObservable: Observable<AnyObject>? = nil
    private var favoriteObservable: Observable<AnyObject>? = nil

    var card: SessionCard! {
        didSet {
            if (liveObservable != nil) {
                liveObservable?.close()
                ratingObservable?.close()
                favoriteObservable?.close()
            }

            title.text = card.session.title

            speakers.text = card.speakers.map({ (speaker) -> String in
                speaker.fullName
            }).joined(separator: ", ")

            liveObservable = card.isLive.onChange(block: { live in
                self.setLive(live: live!.boolValue)
            })

            favoriteObservable = card.isFavorite.onChange(block: { favorite in
                self.setFavorite(favorite: favorite!.boolValue)
            })

            ratingObservable = card.ratingData.onChange(block: { rating in
                self.configureVote(rating: rating)
            })

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

    private func configure() {
        Bundle.main.loadNibNamed("SessionCardView", owner: self, options: nil)
        addSubview(mainView)
        mainView.frame = self.bounds

        isUserInteractionEnabled = true
        voteBar.isHidden = true

        voteBar.layer.shadowColor = UIColor.black.cgColor
        voteBar.layer.shadowOffset = CGSize(width: 0, height: 0.5)
        voteBar.layer.shadowOpacity = 0.15
    }

    private func setLive(live: Bool) {
        liveIcon.isHidden = !live
        liveLabel.isHidden = !live
    }

    private func setFavorite(favorite: Bool) {
        favoriteButton.isSelected = favorite
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

        container?.show(popup: self)
    }

    func hide() {
        UIView.transition(
            with: voteBar,
            duration: 0.3,
            options: [.transitionCrossDissolve],
            animations: {
                self.voteBar.isHidden = true
        },
            completion: nil
        )
    }

    @IBAction func vodeGood(_ sender: Any) {
        voteTouch(.good)
    }

    @IBAction func voteOk(_ sender: Any) {
        voteTouch(.ok)
    }

    @IBAction func voteBad(_ sender: Any) {
        voteTouch(.bad)
    }

    @IBAction func favoriteTouch(_ sender: Any) {
        favoriteTouch()
    }

    private func configureVote(rating: RatingData?) {
        voteUp.isSelected = rating == .good
        voteOk.isSelected = rating == .ok
        voteDown.isSelected = rating == .bad

        let image: UIImage = {
            switch rating {
            case RatingData.good: return UIImage(named: "voteGoodOrangeSmall")!
            case RatingData.ok: return UIImage(named: "voteOkOrangeSmall")!
            case RatingData.bad: return UIImage(named: "voteBadOrangeSmall")!
            default: return UIImage(named: "voteGoodSmall")!
            }
        }()

        voteButton.setImage(image, for: .normal)
        hide()
    }
}
