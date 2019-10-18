import Foundation
import UIKit
import FirebaseAnalytics
import KotlinConfAPI
import Nuke

class MainTabController : UITabBarController {
    override func viewDidLoad() {
        if (Conference.isFirstLaunch()) {
            navigationController?.pushViewController(
                createPage(name: "Welcome"),
                animated: true
            )
        }

        Conference.errors.watch {error in
            self.showError(error: error!)
        }

        setupDiskCache()
        updateHomeController()
        super.viewDidLoad()

        navigationController?.isNavigationBarHidden = true
    }

    func updateHomeController() {
        let time = Conference.now()
        if (time.compareTo(other: TimeKt.CONFERENCE_START) < 0) {
            let mainBoard = UIStoryboard(name: "Main", bundle: nil)
            let beforeView = mainBoard.instantiateViewController(
                withIdentifier: "Before"
            )

            viewControllers?[0] = beforeView

        } else if (time.compareTo(other: TimeKt.CONFERENCE_END) > 0) {
            let mainBoard = UIStoryboard(name: "Main", bundle: nil)
            let afterView = mainBoard.instantiateViewController(
                withIdentifier: "After"
            )

            viewControllers?[0] = afterView
        }
    }

    private func setupDiskCache() {
        DataLoader.sharedUrlCache.diskCapacity = 0

        let pipeline = ImagePipeline {
              let dataCache = try! DataCache(name: "org.jetbrain.kotlinconf.imagecache")
              dataCache.sizeLimit = 200 * 1024 * 1024
              $0.dataCache = dataCache
        }
        ImagePipeline.shared = pipeline
    }
}
