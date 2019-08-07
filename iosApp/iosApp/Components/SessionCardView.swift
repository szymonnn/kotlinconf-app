import Foundation
import UIKit

class SessionCardView : UIView {
    @IBOutlet var mainView: UIView!

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
    }

}
