import SwiftUI
import shared

struct DrawerToDoListsView: View {
    @Environment(\.isPreview) var isPreview
    @Environment(\.previewEnvironment) var previewEnvironment: PreviewEnvironment?
    @ObservedObject private var viewModel: DrawerToDoListsViewModel
    private let customitemViewFactory: (ToDoListCustom) -> DrawerToDoListsCustomItemView

    var body: some View {
        VStack {
            LazyVStack(alignment: .leading) {
                ForEach(viewModel.lists, id: \.uuid) { list in
                    switch onEnum(of: list) {
                        case .custom(let custom):
                            customitemViewFactory(custom)
                        case .predefined:
                            Label(
                                list.title,
                                systemImage: "list.bullet"
                            )
                    }
                    Divider()
                }
            }
            AddToDoListView.factory(previewEnvironment)
        }
        .cornerRadius(CGFloat(12))
        .padding()
    }

    static func factory(_ previewEnvironment: PreviewEnvironment? = nil) -> DrawerToDoListsView {
        let viewModel: DrawerToDoListsViewModel = previewEnvironment?.get() ?? ToDoListsComponent().drawerToDoListsViewModel()
        return DrawerToDoListsView(
            viewModel: viewModel,
            customitemViewFactory: {
                DrawerToDoListsCustomItemView.factory(item: $0, previewEnvironment)
            }
        )
    }
}

private extension DrawerToDoListsViewModel {
    var lists: [ToDoList] {
        get {
            return self.state(
                \.toDoLists,
                 equals: { $0 === $1 },
                 mapper: { items in items.map { $0 as! ToDoList } }
            )
        }
    }
}

struct DrawerToDoListsView_Previews: PreviewProvider {
    static var previews: some View {
        let previewEnvironment = preview {
            $0.drawerToDoListsViewModelFakes(toDoLists: [
                ToDoListCustom(uuid: "", title: "TODO List 1"),
                ToDoListCustom(uuid: "", title: "TODO List 2")
            ])
        }
        DrawerToDoListsView.factory(previewEnvironment)
            .previewEnvironment(previewEnvironment)
    }
}
