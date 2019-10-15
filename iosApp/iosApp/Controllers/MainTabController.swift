import Foundation
import UIKit
import FirebaseAnalytics
import KotlinConfAPI

class MainTabController : UITabBarController {
    override func viewDidLoad() {
        Conference.errors.watch {error in
            self.showError(error: error!)
        }

        super.viewDidLoad()

        navigationController?.isNavigationBarHidden = true

        let time = Conference.now()
        if (time.compareTo(other: TimeKt.CONFERENCE_START) < 0) {
            let mainBoard = UIStoryboard(name: "Main", bundle: nil)
            let beforeView = mainBoard.instantiateViewController(
                withIdentifier: "Before"
            )

            viewControllers?[0] = beforeView

        } else if (time.compareTo(other: TimeKt.CONFERENCE_END) > 0) {
            print("after")
        } else {
            print("Conf")
        }
    }
}
