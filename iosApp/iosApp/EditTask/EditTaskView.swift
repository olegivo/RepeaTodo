//
//  EditTaskView.swift
//  iosApp
//
//  Created by Олег Иващенко on 21.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct EditTaskView: View {
    @Environment(\.isPreview) var isPreview
    @Environment(\.navigator) var navigator: MainNavigatorObservableObject

    @StateObject
    private var viewModel: EditTaskViewModelObservableObject
    @State private var showActionSheet = false

    var body: some View {
        NavigationView {
            Form {
                if viewModel.isLoading {
                    ProgressView()
                } else {
                    Section {
                        titleEditor()
                    }
                    
                    Section {
                        saveButton()
                    }
                }
            }
            .actionSheet(isPresented: $showActionSheet) {
                deleteConfirmation()
            }
            .navigationBarTitle("Edit Todo", displayMode: .inline)
            .toolbar {
                toolbarItemClose()
                toolbarItemDelete()
            }
            .handleNavigation(navigator)
            .alert(
                "Can't load Task",
                isPresented: $viewModel.isLoadingError,
                actions: {
                    Button("OK") {
                        viewModel.wrapped.onCancelClicked()
                    }
                }
            )
            .alert(
                "Save error",
                isPresented: $viewModel.isSaveError,
                actions: { }
            )
        }
    }

    fileprivate func titleEditor() -> TextField<Text> {
        return TextField(
            "Enter A Title Here",
            text: Binding(
                get: {
                    viewModel.title
                },
                set: { v in
                    viewModel.onTitleChanged(v)
                }
            )
        )
    }

    fileprivate func saveButton() -> some View {
        return Button(action: {
            viewModel.wrapped.onSaveClicked()
        }) {
            if viewModel.isSaving {
                ProgressView()
            } else {
                Text("Save")
            }
        }
        .disabled(!viewModel.canSave)
    }
    
    fileprivate func deleteConfirmation() -> ActionSheet {
        return ActionSheet(
            title: Text("Deletion"),
            message: Text("Do you want to delete the '\(viewModel.title)'?"),
            buttons: [
                .cancel { print(self.showActionSheet) },
                .destructive(Text("Delete")) { viewModel.wrapped.onDeleteClicked() }
            ]
        )
    }
    
    fileprivate func toolbarItemClose() -> ToolbarItem<(), Button<Image>> {
        return ToolbarItem(placement: .cancellationAction) {
            Button(action: {
                viewModel.wrapped.onCancelClicked()
            }) {
                Image(systemName: "xmark")
            }
        }
    }
    
    fileprivate func toolbarItemDelete() -> ToolbarItem<(), Button<Image>> {
        return ToolbarItem(placement: .destructiveAction) {
            Button(action: {
                self.showActionSheet = true
            }) {
                Image(systemName: "trash")
            }
        }
    }

    static func factory(uuid: String, isPreview: Bool = false) -> EditTaskView {
        let viewModel = isPreview ? FakeEditTaskViewModel() : EditTaskComponent().editTaskViewModel(uuid: uuid)
        return EditTaskView(viewModel: viewModel.asObservableObject())
    }

}

struct EditTaskView_Previews: PreviewProvider {
    static var previews: some View {
        EditTaskView.factory(uuid: "The UUID", isPreview: true)
    }
}
