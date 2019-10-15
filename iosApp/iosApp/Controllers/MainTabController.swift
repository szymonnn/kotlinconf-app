import Foundation
import UIKit
import FirebaseAnalytics

class MainTabController : UITabBarController {
    override func viewDidLoad() {
        Conference.errors.watch {error in
            self.showError(error: error!)
        }

        super.viewDidLoad()
        navigationController?.isNavigationBarHidden = true
    }
}
