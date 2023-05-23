import SwiftUI
import shared

struct AddToDoListView: View {
    @Environment(\.isPreview) var isPreview
    @ObservedObject private var viewModel: AddToDoListViewModel

    var body: some View {
        HStack {
            if viewModel.state(\.isEditingNew) {
                TextField("Enter A Title Here", text: viewModel.binding(\.title))
                    .padding()
                Button(
                    action: { viewModel.onSaveClicked() },
                    label: {
                        Label(
                            "",
                            systemImage: "checkmark"
                        )
                    }
                )
                .disabled(!viewModel.state(\.canSaveNew))
                Button(
                    action: { viewModel.cancelAddNew() },
                    label: {
                        Label(
                            "",
                            systemImage: "xmark"
                        )
                    }
                )
            } else {
                Button(
                    action: { viewModel.beginAddingNew() },
                    label: {
                        Label(
                            "Add Todo-list",
                            systemImage: "plus"
                        )
                    }
                )
                Spacer()
            }
        }
    }

    static func factory(
        _ previewEnvironment: PreviewEnvironment? = nil
    ) -> AddToDoListView {
        let viewModel: AddToDoListViewModel = previewEnvironment?.get() ?? ToDoListsComponent().addToDoListViewModel()
        return AddToDoListView(viewModel: viewModel)
    }
}

struct AddToDoListView_Previews: PreviewProvider {
    static var previews: some View {
        AddToDoListView.factory( preview { $0.addToDoListViewModelFakes(isEditingNew: false) })
    }
}

struct AddToDoListViewEditing_Previews: PreviewProvider {
    static var previews: some View {
        AddToDoListView.factory( preview { $0.addToDoListViewModelFakes(isEditingNew: true) })
    }
}
