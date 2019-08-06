import Foundation
import UIKit
import KotlinConfAPI

@IBDesignable
class ScheduleTableCell : UITableViewCell {
    @IBOutlet weak var favorite: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var subtitleLabel: UILabel!
    @IBOutlet weak var separatorView: UIView!
    @IBOutlet weak var separatorOffsetConstraint: NSLayoutConstraint!

    @IBOutlet weak var descriptionOffset: NSLayoutConstraint!
    var separatorStyle: UITableViewCell.SeparatorStyle!

//    func configureLook(
//        inFavoriteSection: Bool,
//        isLast: Bool,
//        session: Session
//    ) {
//        separatorInset = UIEdgeInsets(top: 0, left: 100000, bottom: 0, right: 0)
//
//        titleLabel?.text = session.title
//        subtitleLabel?.text = session.room.name + ", " + session.speakers[0].fullName
//        backgroundColor = UIColor.white
//
//        if (isLast || inFavoriteSection) {
//            separatorView.isHidden = true
//        } else {
//            separatorView.isHidden = false
//        }
//
//        if (inFavoriteSection) {
//            self.configureFavorite(data: session)
//            return
//        }
//
//        if (session.startsAt.toReadableDateTimeString() == "2018 Oct 4 10:15") {
//            configureNow(data: session)
//            return
//        }
//
//        configurePlain(data: session)
//    }
//
//    private func configureFavorite(data: Session) {
//        let time = data.startsAt.toReadableTimeString() + "-" + data.endsAt.toReadableTimeString()
//        subtitleLabel?.text = time + ", " + data.room.name
//
//        titleLabel.font = UIFont.headerListMed
//        subtitleLabel.font = UIFont.noteList
//
//        titleLabel.textColor = UIColor.darkGrey
//        subtitleLabel.textColor = UIColor.lightGrey
//
//        titleLabel.numberOfLines = 0
//        titleLabel.lineBreakMode = .byWordWrapping
//
//        favorite.isHidden = false
//
//        separatorOffsetConstraint.constant = 33.0
//        descriptionOffset.constant = 8.0
//
//        selectedBackgroundView = nil
//    }
//
//    private func configureNow(data: Session) {
//        self.backgroundColor = UIColor.white
//
//        titleLabel.font = UIFont.headerListSmall
//        subtitleLabel.font = UIFont.noteListSmall
//
//        titleLabel.textColor = UIColor.darkGrey
//        subtitleLabel.textColor = UIColor.darkGrey50
//        separatorView.backgroundColor = UIColor.lightGrey50
//        separatorView.alpha = 0.5
//
//        titleLabel.numberOfLines = 1
//        titleLabel.lineBreakMode = .byTruncatingTail
//
//        favorite.isHidden = true
//        separatorOffsetConstraint.constant = 33.0
//        descriptionOffset.constant = 1.0
//    }
//
//    private func configurePlain(data: Session) {
//        self.backgroundColor = UIColor.white
//
//        titleLabel.font = UIFont.headerListSmall
//        subtitleLabel.font = UIFont.noteListSmall
//
//        titleLabel.textColor = UIColor.darkGrey
//        subtitleLabel.textColor = UIColor.darkGrey50
//        separatorView.backgroundColor = UIColor.lightGrey50
//        separatorView.alpha = 0.5
//
//        titleLabel.numberOfLines = 1
//        titleLabel.lineBreakMode = .byTruncatingTail
//
//        favorite.isHidden = true
//        separatorOffsetConstraint.constant = 33.0
//        descriptionOffset.constant = 1.0
//
//        selectedBackgroundView = nil
//    }
}
