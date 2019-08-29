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
        scheduleTable.register(UINib(nibName: "ScheduleTableCoffeeBreakBar", bundle: nil), forHeaderFooterViewReuseIdentifier: "ScheduleTableCoffeeBreakBar")

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

            UIView.transition(
                with: self.headerView,
                duration: 0.2,
                options: [],
                animations: {
                    self.searchContainer.isHidden = false
                    self.headerView.isHidden = true
                }, completion: nil
            )
        }

        headerView.addSubview(tableHeader)
    }

    @IBAction func onSearchCancel(_ sender: Any) {
        searchActive = false
        scheduleTable.reloadData()

        UIView.transition(
            with: self.headerView,
            duration: 0.2,
            options: [],
            animations: {
                self.searchContainer.isHidden = true
                self.headerView.isHidden = false
            }, completion: nil
        )
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
        return table.count * 2
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (section % 2 == 0) {
            return 0
        }

        let sectionIndex = section / 2;
        return currentTable[sectionIndex].sessions.count
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if (section == 0) { 
            return nil
        }

        if (section % 2 == 1) {
            let index = section / 2
            let timeHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableHeader") as! ScheduleTableHeader

            let card = currentTable[index]
            timeHeader.configureLook(month: card.month, day: Int(card.day), time: card.title)
            return timeHeader
        }

        let breakHeader = tableView.dequeueReusableHeaderFooterView(withIdentifier: "ScheduleTableCoffeeBreakBar")
        return breakHeader
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let section = indexPath.section / 2
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
        if (section == 0) {
            return 1
        }

        if (section % 2 == 0) {
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
