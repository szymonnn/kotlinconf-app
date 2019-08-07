import Foundation
import UIKit
import KotlinConfAPI

@IBDesignable
class ScheduleController
    : UIViewController, UITableViewDelegate, UITableViewDataSource, UIGestureRecognizerDelegate, ScheduleView {

    @IBOutlet weak var scheduleTable: UITableView!

    private var presenter: SchedulePresenter {
        return SchedulePresenter(view: self)
    }

    lazy var refreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()

        refreshControl.addTarget(
            self,
            action: #selector(ScheduleController.onRefresh(_:)),
            for: UIControl.Event.valueChanged
        )

        return refreshControl
    }()

    private var tableData: [SessionGroup] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.pullToRefresh()
        scheduleTable.addSubview(refreshControl)
//        refreshControl.beginRefreshing()

//        scheduleTable.register(UINib(nibName: ScheduleCellId, bundle: nil), forCellReuseIdentifier: ScheduleCellId)
        scheduleTable.register(UINib(nibName: "ScheduleTableHeader", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleTableHeader")
        scheduleTable.delegate = self
        scheduleTable.dataSource = self
//        scheduleTable.separatorInset = UIEdgeInsets(top: 0, left: 32, bottom: 0, right: 0)
//        scheduleTable.separatorColor = UIColor.darkGrey
    }

    func onSessions(session: [SessionGroup]) {
        print("Load sessions: " + String(session.count))
        tableData = session
        refreshControl.endRefreshing()

        scheduleTable.reloadData()
    }

    func onFavorites(session: [SessionGroup]) {
    }

    func onVotes(session: [String : RatingData]) {
    }

    @IBAction
    func onRefresh(_ refreshControl: UIRefreshControl) {
        presenter.pullToRefresh()
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return tableData.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tableData[section].sessions.count
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
//        if (section == 0) {
//            return nil
//        }
//
//        let sectionSessions = tableData[section].sessions
//        if (sectionSessions.count == 0) {
//            return nil
//        }
//
        let result = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableHeader") as! ScheduleTableHeader
//        let session = sectionSessions[0].session
//
//        let startTime = session.startsAt
//        let endTime = session.endsAt
//        let isNow: Bool = session.startsAt == "2018 Oct 4 10:15"
//        result.configureLook(start: startTime, end: endTime, isNow: isNow)
//
        return result
//        return nil
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let section = indexPath.section
        let row = indexPath.row

        let result = tableView.dequeueReusableCell(withIdentifier: "ScheduleTableCell", for: indexPath)

//        let session = tableData[section].sessions[row].session
//
//        let inFavoriteSection = section == 0
//        let isLast = row + 1 == tableData[section].sessions.count

//        result.configureLook(
//            inFavoriteSection: inFavoriteSection,
//            isLast: isLast,
//            session: session
//        )

        return result
    }

//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        tableView.deselectRow(at: indexPath, animated: false)
//
//        let sessionBoard = UIStoryboard(name: "Main", bundle: nil)
//        let sessionView = sessionBoard.instantiateViewController(withIdentifier: "Session") as! SessionController
//
//        let session = tableData[indexPath.section].sessions[indexPath.item].session
//        sessionView.session = session
//
//        let cell = tableView.cellForRow(at: indexPath)!
//        let oldColor = cell.backgroundColor
//
//        let isNow: Bool = session.startsAt == "2018 Oct 4 10:15"
//        if (isNow) {
//            cell.backgroundColor = UIColor.pressedOrange
//        } else {
//            cell.backgroundColor = UIColor.lightGray
//        }
//
//        self.navigationController?.pushViewController(sessionView, animated: true)
//        navigationController!.interactivePopGestureRecognizer!.isEnabled = true
//        cell.backgroundColor = oldColor
//    }
//
//    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
//        if (section == 0) {
//            return 1
//        }
//
//        return 32
//    }

    public override func showError(error: KotlinThrowable) {
        scheduleTable.contentOffset = CGPoint.zero
        refreshControl.endRefreshing()
        super.showError(error: error)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        navigationController!.interactivePopGestureRecognizer!.delegate = self
        navigationController!.interactivePopGestureRecognizer!.isEnabled = false
    }
}
