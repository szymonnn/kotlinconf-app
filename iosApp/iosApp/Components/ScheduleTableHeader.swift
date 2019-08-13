import Foundation
import UIKit

class ScheduleTableHeader : UITableViewHeaderFooterView {
    @IBOutlet weak var header: UILabel!

    func configureLook(title: String) {
        header.text = title
    }
}
