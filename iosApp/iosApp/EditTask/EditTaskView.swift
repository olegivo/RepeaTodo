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

    @StateObject
    private var viewModel: EditTaskViewModelObservableObject

    var body: some View {
        NavigationView {
            Form {
                if viewModel.isLoading {
                    ProgressView()
                } else {
                    Section {
                        TextField(
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
                    
                    Section {
                        Button(action: {
                            viewModel.onSaveClicked()
                        }) {
                            if viewModel.isSaving {
                                ProgressView()
                            } else {
                                Text("Save")
                            }
                        }
                        .disabled(!viewModel.canSave)
                    }
                }
            }
            .navigationBarTitle("Edit Todo", displayMode: .inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(action: {
                        viewModel.onCancelClicked()
                    }) {
                        Image(systemName: "xmark")
                    }
                }
            }
            .handleNavigation($viewModel.navigationDirection)
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
