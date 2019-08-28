import Foundation
import UIKit

class ParntersController : UIViewController {
    @IBAction func backButtonTouchUp(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func buttonTouch(_ sender: Any) {
        let board = UIStoryboard(name: "Main", bundle: nil)
        let controller = board.instantiateViewController(withIdentifier: "Partner")
        self.navigationController?.pushViewController(controller, animated: true)
    }
}

class ParnterController : UIViewController {
    @IBAction func backButtonTouchUp(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func openWebsite(_ sender: Any) {
        let urlString = (sender as! UIButton).titleLabel!.text!
        let url = URL(string: urlString)

        UIApplication.shared.open(url!)
    }
}
