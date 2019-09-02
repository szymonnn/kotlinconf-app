import Foundation
import UIKit
import KotlinConfAPI

enum Section {
    case all
    case favorites
}

class ScheduleController : UIViewController, UITableViewDelegate, UITableViewDataSource, BaloonContainer {
    @IBOutlet weak var scheduleTable: UITableView!
    @IBOutlet weak var headerView: UIView!
    @IBOutlet weak var searchContainer: UIView!
    @IBOutlet weak var stackView: UIStackView!

    var activePopup: UIView? = nil

    let tableHeader = UINib(nibName: "ScheduleHeader", bundle: nil).instantiate(withOwner: nil, options: [:])[0] as! ScheduleHeader

    lazy var refreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()

        refreshControl.addTarget(
            self,
            action: #selector(ScheduleController.onRefresh(_:)),
            for: UIControl.Event.valueChanged
        )

        return refreshControl
    }()

    private var all: [SessionGroup] = []
    private var favorites: [SessionGroup] = []
    private var search: [SessionCard] = []

    private var searchActive = false
    private var section: Section = .all

    private var currentTable: [SessionGroup] {
        get {
            return (section == .all) ? all : favorites
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        scheduleTable.addSubview(refreshControl)
        scheduleTable.register(UINib(nibName: "ScheduleTableHeader", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleTableHeader")
        scheduleTable.register(UINib(nibName: "ScheduleTableSmallHeader", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleTableSmallHeader")

        configureTableHeader()

        scheduleTable.delegate = self
        scheduleTable.dataSource = self

        Conference.schedule.onChange(block: {data in
            self.onSessions(session: data as! [SessionGroup])
        })

        Conference.favoriteSchedule.onChange(block: {data in
            self.onFavorites(session: data as! [SessionGroup])
        })

        self.view.isUserInteractionEnabled = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        searchContainer.isHidden = true
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
    }

    func configureTableHeader() {
        tableHeader.onAllTouch = {
            self.section = .all
            self.scheduleTable.reloadData()
        }

        tableHeader.onFavoritesTouch = {
            self.section = .favorites
            self.scheduleTable.reloadData()
        }

        tableHeader.onSearchTouch = {
            self.searchActive = true
            self.scheduleTable.reloadData()

            self.searchContainer.isHidden = false
            self.headerView.isHidden = true
//            UIView.transition(
//                with: self.headerView,
//                duration: 0.2,
//                options: [],
//                animations: {
//                    self.searchContainer.isHidden = false
//                    self.headerView.isHidden = true
//                }, completion: nil
//            )
        }

        headerView.addSubview(tableHeader)
    }

    @IBAction func onSearchCancel(_ sender: Any) {
        searchActive = false
        scheduleTable.reloadData()

        self.searchContainer.isHidden = true
        self.headerView.isHidden = false
//        UIView.transition(
//            with: self.headerView,
//            duration: 0.2,
//            options: [],
//            animations: {
//                self.searchContainer.isHidden = true
//                self.headerView.isHidden = false
//            }, completion: nil
//        )
    }

    func onSessions(session: [SessionGroup]) {
        all = session
        refreshControl.endRefreshing()
        if (section == .all) {
            scheduleTable.reloadData()
        }
    }

    func onFavorites(session: [SessionGroup]) {
        favorites = session
        if (section == .favorites) {
            scheduleTable.reloadData()
        }
    }

    @IBAction
    func onRefresh(_ refreshControl: UIRefreshControl) {
        Conference.refresh()
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        if (searchActive) {
            return 1
        }

        let table = (section == .all) ? all : favorites
        return table.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return currentTable[section].sessions.count
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let card = currentTable[section]

        if (card.daySection) {
            let breakHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableSmallHeader") as! ScheduleTableSmallHeader
            breakHeader.displayDay(title: card.title)
            return breakHeader
        }

        if (card.lunchSection) {
            let breakHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableSmallHeader") as! ScheduleTableSmallHeader
            breakHeader.displayLunch(title: card.title)
            return breakHeader
        }

        let timeHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableHeader") as! ScheduleTableHeader
        timeHeader.configureLook(month: card.month, day: Int(card.day), time: card.title)
        return timeHeader
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let section = indexPath.section
        let row = indexPath.row

        let result = tableView.dequeueReusableCell(withIdentifier: "ScheduleTableCell", for: indexPath) as! ScheduleTableCell
        let card = currentTable[section].sessions[row]
        configureCell(cell: result, card: card)

        return result
    }

    private func configureCell(cell: ScheduleTableCell, card: SessionCard) {
        let item = cell.card!
        item.card = card
        item.baloonContainer = self

        cell.card.onTouch = {
            self.showSession(card: card)
        }
    }

    private func showSession(card: SessionCard) {
        let sessionBoard = UIStoryboard(name: "Main", bundle: nil)
        let sessionView = sessionBoard.instantiateViewController(withIdentifier: "Session") as! SessionController

        sessionView.card = card
        self.navigationController?.pushViewController(sessionView, animated: true)
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        let group = currentTable[section]
        if (group.daySection || group.lunchSection) {
            return 42
        }

        return 120
    }

    private let bounceGap = CGFloat(10.0)
    private var startOffset = CGFloat(0.0)

    private var active: Baloon? = nil
    func show(popup: Baloon) {
        if (active != nil) {
            active?.hide()
        }

        active = popup
    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        startOffset = scrollView.contentOffset.y
        if active != nil {
            active!.hide()
            active = nil
        }

        if (activePopup != nil) {
            activePopup?.isHidden = true
            activePopup = nil
        }
    }

    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if (searchActive) {
            return
        }
        let currentOffset = scrollView.contentOffset.y
        if (currentOffset > startOffset + bounceGap && !headerView.isHidden) {
            UIView.transition(
                with: headerView,
                duration: 0.2,
                options: [],
                animations: {
                    self.headerView.isHidden = true
            }, completion: nil
            )
        }

        if (currentOffset + bounceGap < startOffset && headerView.isHidden) {
            UIView.transition(
                with: headerView,
                duration: 0.2,
                options: [],
                animations: {
                    self.headerView.isHidden = false
                }, completion: nil
            )
        }
    }

    public override func showError(error: KotlinThrowable) {
        scheduleTable.contentOffset = CGPoint.zero
        refreshControl.endRefreshing()
        super.showError(error: error)
    }

}
