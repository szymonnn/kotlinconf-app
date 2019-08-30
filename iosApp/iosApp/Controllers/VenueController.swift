import Foundation
import UIKit
import Mapbox

enum Floor {
    case ground
    case first
}

class VenueController : UIViewController, MGLMapViewDelegate {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var logoView: UIImageView!
    @IBOutlet weak var sessionsView: UIScrollView!
    
    @IBOutlet weak var mapView: MGLMapView!
    @IBOutlet weak var dragBar: UIView!
    @IBOutlet weak var overlay: UIView!

    @IBOutlet weak var groundFloor: TopButton!
    @IBOutlet weak var firstFloor: TopButton!

    private var initial: CGFloat = 33.0
    private var floor: Floor = .ground
    private var descriptionActive: Bool = false

    override func viewDidLoad() {
        super.viewDidLoad()

        mapView.delegate = self

        let gesture = UIPanGestureRecognizer(
            target: self,
            action: #selector(VenueController.wasDragged(_:))
        )
        dragBar.addGestureRecognizer(gesture)
        dragBar.isUserInteractionEnabled = true

        let singleTap = UITapGestureRecognizer(target: self, action: #selector(handleMapTap(sender:)))
        mapView.addGestureRecognizer(singleTap)
    }

    override func viewDidAppear(_ animated: Bool) {
        hideDescription()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.deepSkyBlue

        showFloor()
    }

    @IBAction func groundFloorSelect(_ sender: Any) {
        floor = .ground
        showFloor()
    }

    @IBAction func firstFloorSelect(_ sender: Any) {
        floor = .first
        showFloor()
    }

    @IBAction func closeTouch(_ sender: Any) {
        hideDescription()
    }

    private func showFloor() {
        if (floor == .ground) {
            mapView.styleURL = URL(string: "mapbox://styles/denisvoronov1/cjzikqjgb41rf1cnnb11cv0xw")
            groundFloor.dark()
            firstFloor.light()
        } else {
            mapView.styleURL = URL(string: "mapbox://styles/denisvoronov1/cjzsessm40k341clcoer2tn9v")
            groundFloor.light()
            firstFloor.dark()
        }
    }

    @objc func wasDragged(_ gestureRecognizer: UIPanGestureRecognizer) {
        let translation = gestureRecognizer.translation(in: self.view)

        overlay.center = CGPoint(x: overlay.center.x, y: overlay.center.y + translation.y)
        gestureRecognizer.setTranslation(CGPoint.zero, in: self.view)

        if gestureRecognizer.state == .ended {
            let top = self.overlay.frame.origin.y

            if (!descriptionActive && top - initial > 50) {
                showDescription()
                return
            }

            if (descriptionActive && top < self.overlay.frame.height - 50) {
                hideDescription()
                return
            }
        }
    }

    func showDescription() {
        descriptionActive = true

        UIView.transition(
            with: overlay,
            duration: 0.3,
            options: [],
            animations: {
                self.overlay.frame.origin.y = self.initial
        },
            completion: nil
        )
    }

    func hideDescription() {
        descriptionActive = false

        UIView.transition(
            with: overlay,
            duration: 0.3,
            options: [],
            animations: {
                self.overlay.frame.origin.y = self.overlay.frame.height - 100
        },
            completion: nil
        )
    }

    @objc @IBAction func handleMapTap(sender: UITapGestureRecognizer) {
        let spot = sender.location(in: mapView)

        let features = mapView.visibleFeatures(at: spot)

        if let feature = features.first, let state = feature.attribute(forKey: "name") as? String {
            showNote(state)
        }
    }

    func showNote(_ name: String) {
        titleLabel.text = name
        showDescription()
    }

}
