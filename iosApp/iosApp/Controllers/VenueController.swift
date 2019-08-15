import Foundation
import UIKit
import Mapbox

class VenueController : UIViewController {
    @IBOutlet weak var mapView: MGLMapView!

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.deepSkyBlue
    }
}
