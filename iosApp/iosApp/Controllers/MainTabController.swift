import Foundation
import UIKit
import FirebaseAnalytics
import KotlinConfAPI
import Nuke

class MainTabController : UITabBarController {
    override func viewDidLoad() {
        Conference.errors.watch {error in
            self.showError(error: error!)
        }

        setupDiskCache()
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
            let mainBoard = UIStoryboard(name: "Main", bundle: nil)
            let beforeView = mainBoard.instantiateViewController(
                withIdentifier: "After"
            )

            viewControllers?[0] = beforeView
        }
    }

    private func setupDiskCache() {
        DataLoader.sharedUrlCache.diskCapacity = 0

        let pipeline = ImagePipeline {
              let dataCache = try! DataCache(name: "org.jetbrain.kotlinconf.imagecache")
              dataCache.sizeLimit = 200 * 1024 * 1024
              $0.dataCache = dataCache
        }

        // 5
        ImagePipeline.shared = pipeline
    }
}
