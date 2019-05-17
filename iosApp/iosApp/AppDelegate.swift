import UIKit
import KotlinConfAPI

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    public static let service: ConferenceService = ConferenceService(
        userId: generateUuid(),
//        endPoint: "http://localhost:8080/",
        endPoint: "https://api.kotlinconf.com/",
        storage: IosStorage()
    )

    private static func generateUuid() -> String {
        return "ios-" + (UIDevice.current.identifierForVendor ?? UUID()).uuidString
    }
}
