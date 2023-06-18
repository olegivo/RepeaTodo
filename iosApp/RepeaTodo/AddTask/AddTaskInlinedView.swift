import SwiftUI
import shared

struct AddTaskInlinedView: View {
    @ObservedObject private var viewModel: AddTaskViewModel

    var body: some View {
        HStack {
            TextField("Enter A Title Here", text: viewModel.binding(\.title))
                .padding()
            Button(action: {
                viewModel.onAddClicked()
            }) {
                if viewModel.state(\.isAdding) {
                    ProgressView()
                } else {
                    Image(systemName: "plus")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(.white)
                        .frame(width: 24, height: 24)
                        .padding()
                        .background(Color.accentColor)
                        .cornerRadius(12)
                }
            }
            .disabled(!viewModel.state(\.canAdd))
        }
    }

    static func factory(_ previewEnvironment: PreviewEnvironment? = nil) -> AddTaskInlinedView {
        let viewModel: AddTaskViewModel = previewEnvironment?.get() ?? AddTaskComponent().addTaskViewModel()
        return AddTaskInlinedView(viewModel: viewModel)
    }
}

struct AddTaskInlinedView_Previews: PreviewProvider {
    static var previews: some View {
        AddTaskInlinedView.factory(preview{ $0.addTaskViewModelWithFakes() })
    }
}
