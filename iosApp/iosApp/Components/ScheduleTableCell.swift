import Foundation
import UIKit

class ScheduleTableCell : UITableViewCell {
    @IBOutlet weak var card: SessionCardView!
    var touchHandler: (() -> Void)? = nil

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        configure()
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        touchHandler?()
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        card.mainView.backgroundColor = UIColor.white
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        card.mainView.backgroundColor = UIColor.white
    }

    private func configure() {
        isUserInteractionEnabled = true
    }
}
