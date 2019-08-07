import Foundation
import UIKit
import KotlinConfAPI

extension UIViewController: BaseView {
    
    public func showError(error: KotlinThrowable) {
        print("ERROR")
        print("-----------------------------")
        error.printStackTrace()
        print("-----------------------------")

        var title: String = "Error"
        var errorMessage: String!

        switch error {
        case is Unauthorized:
            errorMessage = "Unauthorized"
        case is CannotFavorite:
            errorMessage = "Cannot set favorite now"
        case is CannotPostVote:
            title = "Not Allowed"
            errorMessage = "Failed to rate sessions, please check your connection"
        case is CannotDeleteVote:
            title = "Not Allowed"
            errorMessage = "Failed to update session rating, please check your connection"
        case is UpdateProblem:
            errorMessage = "Failed to get data from server, please check your internet connection"
        case is TooEarlyVote:
            title = "Not Allowed"
            errorMessage = "You cannot rate the session before it starts"
        case is TooLateVote:
            title = "Not Allowed"
            errorMessage = "Rating is only permitted up to 15 minutes after the session end"
        default:
            errorMessage = "Unknown Error"
        }
        
        self.showError(title: title, message: errorMessage)
    }

    func showError(title: String, message: String) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
        alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertAction.Style.default,handler: nil))
        self.present(alertController, animated: true, completion: nil)
    }
}
