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

//        scheduleTable.register(UINib(nibName: "ScheduleHeader", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleHeader")
        scheduleTable.register(UINib(nibName: "ScheduleTableHeader", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleTableHeader")
        scheduleTable.register(UINib(nibName: "ScheduleTableCoffeeBreakBar", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleTableCoffeeBreakBar")


        scheduleTable.delegate = self
        scheduleTable.dataSource = self

        let tableHeader = UINib(nibName: "ScheduleHeader", bundle: nil).instantiate(withOwner: nil, options: [:])[0] as! UIView
        scheduleTable.tableHeaderView = tableHeader

    }

    func onSessions(session: [SessionGroup]) {
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
        return tableData.count * 2
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (section % 2 == 0) {
            return 0
        }

        let sectionIndex = section / 2;
        return tableData[sectionIndex].sessions.count
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if (section == 0) {
            return nil
        }
//
//        let sectionSessions = tableData[section].sessions
//        if (sectionSessions.count == 0) {
//            return nil
//        }
//
        if (section % 2 == 1) {
            let index = section / 2
            let timeHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableHeader") as! ScheduleTableHeader
            timeHeader.configureLook(title: tableData[index].groupName)
            return timeHeader
        }


//        let session = sectionSessions[0].session

//        let startTime = session.startsAt
//        let endTime = session.endsAt
//        let isNow: Bool = session.startsAt == "2018 Oct 4 10:15"
//        result.configureLook(start: startTime, end: endTime, isNow: isNow)

        let breakHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableCoffeeBreakBar")
        return breakHeader
//        return nil
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let section = indexPath.section / 2
        let row = indexPath.row

        let result = tableView.dequeueReusableCell(withIdentifier: "ScheduleTableCell", for: indexPath) as! ScheduleTableCell
        let card = tableData[section].sessions[row]
        result.card.card = card

//        let inFavoriteSection = section == 0
//        let isLast = row + 1 == tableData[section].sessions.count

//        result.configureLook(
//            inFavoriteSection: inFavoriteSection,
//            isLast: isLast,
//            session: session
//        )

        return result
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)

        let sessionBoard = UIStoryboard(name: "Main", bundle: nil)
        let sessionView = sessionBoard.instantiateViewController(withIdentifier: "Session") as! SessionController

        let section = indexPath.section / 2

        let session = tableData[section].sessions[indexPath.item].session
        sessionView.session = session

        let cell = tableView.cellForRow(at: indexPath)!
        let oldColor = cell.backgroundColor

        let isNow: Bool = session.startsAt == "2018 Oct 4 10:15"
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

        if (section % 2 == 0) {
            return 42
        }

        return 120
    }

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
