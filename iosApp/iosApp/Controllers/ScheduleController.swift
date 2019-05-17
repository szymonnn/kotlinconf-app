import Foundation
import UIKit
import KotlinConfAPI

let ScheduleCellId = "ScheduleTableCell"
let ScheduleHeaderId = "ScheduleTableHeader"

@IBDesignable
class ScheduleController : UIViewController, UITableViewDelegate, UITableViewDataSource, ScheduleView, UIGestureRecognizerDelegate {
    @IBOutlet weak var scheduleTable: UITableView!

    private var presenter: SchedulePresenter {
        return SchedulePresenter(
            view: self, service: AppDelegate.service
        )
    }

    lazy var refreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()

        refreshControl.addTarget(
            self,
            action: #selector(ScheduleController.onRefresh(_:)),
            for: UIControlEvents.valueChanged
        )

        return refreshControl
    }()

    private var tableData: [[Session]] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationController!.interactivePopGestureRecognizer!.delegate = self


        scheduleTable.addSubview(refreshControl)
        refreshControl.beginRefreshing()
        presenter.onCreate()

        scheduleTable.register(UINib(nibName: ScheduleCellId, bundle: nil), forCellReuseIdentifier: ScheduleCellId)
        scheduleTable.register(UINib(nibName: ScheduleHeaderId, bundle: nil), forHeaderFooterViewReuseIdentifier: ScheduleHeaderId)
        scheduleTable.delegate = self
        scheduleTable.dataSource = self
        scheduleTable.separatorInset = UIEdgeInsets(top: 0, left: 32, bottom: 0, right: 0)
        scheduleTable.separatorColor = UIColor.darkGrey

        navigationController?.isNavigationBarHidden = true
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        navigationController!.interactivePopGestureRecognizer!.isEnabled = false
    }
    

    @IBAction
    func onRefresh(_ refreshControl: UIRefreshControl) {
        presenter.onPullRefresh()
    }

    func onUpdate(sessions: [[Session]], favorites: [Session]) {
        tableData = [favorites] + sessions
        scheduleTable.reloadData()
        refreshControl.endRefreshing()
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return tableData.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableData[section].count
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if (section == 0) {

            return nil
        }

        let sectionSessions = tableData[section]
        if (sectionSessions.count == 0) {
            return nil
        }

        let result = tableView.dequeueReusableHeaderFooterView(withIdentifier: ScheduleHeaderId) as! ScheduleTableHeader
        let session = sectionSessions[0]

        let startTime = session.startsAt.toReadableTimeString()
        let endTime = session.endsAt.toReadableTimeString()
        let isNow: Bool = session.startsAt.toReadableDateTimeString() == "2018 Oct 4 10:15"
        result.configureLook(start: startTime, end: endTime, isNow: isNow)

        return result
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        let section = indexPath.section
        let row = indexPath.row

        let result = tableView.dequeueReusableCell(withIdentifier: ScheduleCellId, for: indexPath) as! ScheduleTableCell

        let session = tableData[section][row]
        let inFavoriteSection = section == 0
        let isLast = row + 1 == tableData[section].count
        
        result.configureLook(
            inFavoriteSection: inFavoriteSection,
            isLast: isLast,
            session: session
        )

        return result
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)

        let sessionBoard = UIStoryboard(name: "Main", bundle: nil)
        let sessionView = sessionBoard.instantiateViewController(withIdentifier: "Session") as! SessionController
        let session = tableData[indexPath.section][indexPath.item]
        sessionView.session = session

        let cell = tableView.cellForRow(at: indexPath)!
        let oldColor = cell.backgroundColor

        let isNow: Bool = session.startsAt.toReadableDateTimeString() == "2018 Oct 4 10:15"
        if (isNow) {
            cell.backgroundColor = UIColor.pressedOrange
        } else {
            cell.backgroundColor = UIColor.lightGray
        }

        self.navigationController?.pushViewController(sessionView, animated: true)
        navigationController!.interactivePopGestureRecognizer!.isEnabled = true

        cell.backgroundColor = oldColor
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if (section == 0) {
            return 1
        }

        return 32
    }

    public override func showError(error: KotlinThrowable) {
        scheduleTable.contentOffset = CGPoint.zero
        refreshControl.endRefreshing()
        super.showError(error: error)
    }
}

