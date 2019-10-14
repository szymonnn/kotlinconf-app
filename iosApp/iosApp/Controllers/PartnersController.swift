import Foundation
import UIKit


class ParntersController : UIViewController {
    @IBAction func backButtonTouchUp(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func buttonTouch(_ sender: UIButton) {
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: "Partner")
        (controller as! PartnerController).partnerId = sender.tag
        self.navigationController?.pushViewController(controller, animated: true)
    }
}

