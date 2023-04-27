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

    @ObservedObject private var viewModel: EditTaskViewModel
    @State private var showActionSheet = false

    var body: some View {
        NavigationView {
            Form {
                if viewModel.state(\.isLoading) {
                    ProgressView()
                } else {
                    Section(header: Text("Title")) {
                        titleEditor()
                    }
                    Section(header: Text("Days periodicity")) {
                        daysPeriodicityEditor()
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
                isPresented: Binding(
                    get: { viewModel.state(\.isLoadingError) },
                    set: {_,_ in }
                ),
                actions: {
                    Button("OK") {
                        viewModel.onCancelClicked()
                    }
                }
            )
            .alert(
                "Save error",
                isPresented: Binding(
                    get: { viewModel.state(\.isSaveError) },
                    set: {_,_ in }
                ),
                actions: { }
            )
        }
    }

    fileprivate func titleEditor() -> TextField<Text> {
        return TextField(
            "Enter a title here",
            text: viewModel.binding(\.title)
        )
    }

    fileprivate func daysPeriodicityEditor() -> some View {
        TextField(
            "Enter a days periodicity here",
            text: viewModel.binding(\.daysPeriodicity)
        )
        .keyboardType(.numberPad)
    }

    fileprivate func saveButton() -> some View {
        return Button(action: {
            viewModel.onSaveClicked()
        }) {
            if viewModel.state(\.isSaving) {
                ProgressView()
            } else {
                Text("Save")
            }
        }
        .disabled(!viewModel.state(\.canSave))
    }
    
    fileprivate func deleteConfirmation() -> ActionSheet {
        return ActionSheet(
            title: Text("Deletion"),
            message: Text("Do you want to delete the '\(viewModel.titleBinding.wrappedValue)'?"),
            buttons: [
                .cancel { print(self.showActionSheet) },
                .destructive(Text("Delete")) { viewModel.onDeleteClicked() }
            ]
        )
    }
    
    fileprivate func toolbarItemClose() -> ToolbarItem<(), Button<Image>> {
        return ToolbarItem(placement: .cancellationAction) {
            Button(action: {
                viewModel.onCancelClicked()
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

    static func factory(uuid: String, _ previewEnvironment: PreviewEnvironment? = nil) -> EditTaskView {
        let viewModel: EditTaskViewModel = previewEnvironment?.get() ?? EditTaskComponent().editTaskViewModel(uuid: uuid)
        return EditTaskView(viewModel: viewModel)
    }
}

extension EditTaskViewModel {
    var titleBinding: Binding<String> {
        get {
            return self.binding(\.title)
        }
    }
}

struct EditTaskView_Previews: PreviewProvider {
    static var previews: some View {
        EditTaskView.factory(
            uuid: "The UUID",
            preview{
                $0.editTaskViewModelWithFakes(
                    loadResult: WorkStateCompleted(
                        result: Task(
                            uuid: "The UUID",
                            title: "Task 1",
                            daysPeriodicity: 1,
                            lastCompletionDate: nil
                        )
                    )
                )
            }
        )
    }
}
