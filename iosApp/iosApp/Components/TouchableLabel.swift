import Foundation
import UIKit

class TouchableLabel : UILabel {
    var onTouchUp: () -> Void = {}

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        isUserInteractionEnabled = true
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        onTouchUp()
    }
}
