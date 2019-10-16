import UIKit
import KotlinConfAPI

class BeforeController : UIViewController {
    private var timerWatcher: Ktor_ioCloseable!

    @IBOutlet weak var daysText: UILabel!
    @IBOutlet weak var hoursText: UILabel!
    @IBOutlet weak var minutesText: UILabel!
    @IBOutlet weak var secondsText: UILabel!

    override func viewDidLoad() {
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .dark
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        timerWatcher = Conference.beforeTimer.watch { timer in
            self.daysText.text = String(timer!.days)
            self.hoursText.text = String(timer!.hours)
            self.minutesText.text = String(timer!.minutes)
            self.secondsText.text = String(timer!.seconds)

            if (timer!.days == 0 && timer!.hours == 0 && timer!.minutes == 0 && timer!.seconds == 0) {
                (self.tabBarController as? MainTabController)?.updateHomeController()
                self.navigationController?.popViewController(animated: false)
            }
        }
    }

    override func viewDidDisappear(_ animated: Bool) {
        timerWatcher?.close()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
    }
}
