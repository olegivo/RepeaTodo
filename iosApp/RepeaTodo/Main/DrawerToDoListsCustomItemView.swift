
import SwiftUI
import shared
import Combine

struct DrawerToDoListsCustomItemView: View {
    @Environment(\.isPreview) var isPreview
    @StateObject private var viewModel: DrawerToDoListsCustomItemViewModel
    @State private var showDeleteConfirmation = false

    var body: some View {
        VStack {
            if viewModel.state(\.showDeleteConfirmation) {
                confirmDeleteView()
            } else if viewModel.state(\.isEditing) {
                editingView()
            } else {
                displayView()
            }
        }
    }

    private func displayView() -> some View {
        HStack(spacing: 16) {
            Label(
                viewModel.binding(\.title).wrappedValue,
                systemImage: "list.bullet"
            )
            Spacer()
            Button(
                action: {
                    viewModel.onBeginEditClicked()
                },
                label:  {
                    Image(systemName: "pencil")
                }
            )
            Button(
                action: {
                    viewModel.onDeleteClicked()
                },
                label:  {
                    Image(systemName: "trash")
                }
            )
        }
    }

    private func editingView() -> some View {
        HStack(spacing: 16) {
            Label("", systemImage: "list.bullet")
            TextField("Enter A Title Here", text: viewModel.binding(\.title))
                .padding()
            Spacer()
            Button(
                action: { viewModel.onSaveClicked() },
                label: {
                    Label(
                        "",
                        systemImage: "checkmark"
                    )
                }
            )
            Button(
                action: { viewModel.onCancelEditClicked() },
                label: {
                    Label(
                        "",
                        systemImage: "xmark"
                    )
                }
            )
        }
    }

    private func confirmDeleteView() -> some View {
        HStack(spacing: 16) {
            Label(
                viewModel.binding(\.title).wrappedValue,
                systemImage: "trash"
            )
            Spacer()
            Button(
                action: { viewModel.onDeleteConfirmed() },
                label: {
                    Label(
                        "",
                        systemImage: "checkmark"
                    )
                }
            )
            Button(
                action: { viewModel.onDeleteDismissed() },
                label: {
                    Label(
                        "",
                        systemImage: "xmark"
                    )
                }
            )
        }
    }

    static func factory(
        item: ToDoListCustom,
        _ previewEnvironment: PreviewEnvironment? = nil
    ) -> DrawerToDoListsCustomItemView {
        let viewModel: DrawerToDoListsCustomItemViewModel = previewEnvironment?.get(item) ?? ToDoListsComponent().drawerToDoListsCustomItemViewModel(item: item)
        return DrawerToDoListsCustomItemView(viewModel: viewModel)
    }
}

struct DrawerToDoListsCustomItemView_Previews: PreviewProvider {
    static var previews: some View {
        let item = ToDoListCustom(
            uuid: "",
            title: "Todo list 1"
        )
        DrawerToDoListsCustomItemView.factory(
            item: item,
            preview {
                $0.drawerToDoListsCustomItemViewModelFakes(isEditing: false, isDeleting: false, showDeleteConfirmation: false)
            }
        )
        .padding(16)
    }
}

struct DrawerToDoListsCustomItemViewEditing_Previews: PreviewProvider {
    static var previews: some View {
        let item = ToDoListCustom(
            uuid: "",
            title: "Todo list 1"
        )
        DrawerToDoListsCustomItemView.factory(
            item: item,
            preview {
                $0.drawerToDoListsCustomItemViewModelFakes(isEditing: true, isDeleting: false, showDeleteConfirmation: false)
            }
        )
        .padding(16)
    }
}

struct DrawerToDoListsCustomItemViewDeleteConfirm_Previews: PreviewProvider {
    static var previews: some View {
        let item = ToDoListCustom(
            uuid: "",
            title: "Todo list 1"
        )
        DrawerToDoListsCustomItemView.factory(
            item: item,
            preview {
                $0.drawerToDoListsCustomItemViewModelFakes(isEditing: false, isDeleting: false, showDeleteConfirmation: true)
            }
        )
        .padding(16)
    }
}
