import Foundation
import UIKit

class MainTabController : UITabBarController {
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationController?.isNavigationBarHidden = true
    }
}
