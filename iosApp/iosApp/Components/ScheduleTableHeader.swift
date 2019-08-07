import Foundation
import UIKit

class ScheduleTableHeader : UITableViewHeaderFooterView {
    @IBOutlet weak var backView: UIView!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var nowLabel: UILabel!
    @IBOutlet weak var separatorView: UIView!

    func configureLook(start: String, end: String, isNow: Bool) {
        timeLabel?.text = start + " . . . " + end

        if (isNow) {
            backView.backgroundColor = UIColor.redOrange
            separatorView.backgroundColor = UIColor.white
            nowLabel.isHidden = false
            separatorView.isHidden = false
            separatorView.alpha = 0.5
        } else {
            backView.backgroundColor = UIColor.lightGrey
            nowLabel.isHidden = true
            separatorView.isHidden = true
        }
    }
}
