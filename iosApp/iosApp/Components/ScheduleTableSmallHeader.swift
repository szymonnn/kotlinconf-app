import UIKit

class ScheduleTableSmallHeader : UITableViewHeaderFooterView {
    @IBOutlet weak var title: UILabel!

    func displayDay(title: String) {
        self.title.text = title
        self.title.textColor = UIColor.dayGray
//        animate()
    }

    func displayLunch(title: String) {
        self.title.text = title
        self.title.textColor = UIColor.redOrange

//        animate()
    }


    private func animate() {
        UIView.transition(with: title, duration: 10.0, options: [.curveLinear], animations: {
            self.title.frame.origin.x -= 100
        }) { _ in
            self.animate()
            print("before", self.title.frame.origin.x)
            self.title.frame.origin.x = self.frame.origin.x
        }

    }
}
