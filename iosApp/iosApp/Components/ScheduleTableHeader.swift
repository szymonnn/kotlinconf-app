import Foundation
import UIKit

class ScheduleTableHeader : UITableViewHeaderFooterView {
    @IBOutlet weak var monthLabel: UILabel!
    @IBOutlet weak var dayLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!

    func configureLook(month: String, day: Int, time: String) {
        monthLabel.text = month.uppercased()
        dayLabel.text = String(day)
        timeLabel.text = time
    }
}
