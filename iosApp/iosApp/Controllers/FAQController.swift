import Foundation
import UIKit

class FAQController : UIViewController {
    @IBAction func backButtonTouchUp(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
