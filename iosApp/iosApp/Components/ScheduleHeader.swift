import Foundation
import UIKit

class ScheduleHeader : UIView {
    @IBOutlet weak var allSessions: TopButton!
    @IBOutlet weak var favorites: TopButton!

    var onSearchTouch: () -> Void = {}
    var onAllTouch: () -> Void = {}
    var onFavoritesTouch: () -> Void = {}

    @IBAction func allSessionsTouch(_ sender: Any) {
        configureButtonDark(button: allSessions)
        configureButtonLight(button: favorites)
        onAllTouch()
    }

    @IBAction func favoritesTouch(_ sender: Any) {
        configureButtonDark(button: favorites)
        configureButtonLight(button: allSessions)
        onFavoritesTouch()
    }

    @IBAction func onSearchTouch(_ sender: Any) {
        onSearchTouch()
    }

    private func configureButtonDark(button: TopButton) {
        button.isSelected = true
        button.backgroundColor = UIColor.darkGrey
    }

    private func configureButtonLight(button: TopButton) {
        button.isSelected = false
        button.backgroundColor = UIColor.white
    }
}
