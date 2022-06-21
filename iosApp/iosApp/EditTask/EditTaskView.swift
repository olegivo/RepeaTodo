//
//  EditTaskView.swift
//  iosApp
//
//  Created by Олег Иващенко on 21.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct EditTaskView: View {
    @Environment(\.isPreview) var isPreview

    @StateObject
    private var viewModel: EditTaskViewModelObservableObject

    var body: some View {
        NavigationView {
            Form {
                Section {
                    TextField("Enter A Title Here", text: $viewModel.title)
                }
                
                Section {
                    Button(action: {
                        viewModel.onSaveClicked()
                    }) {
                        if viewModel.isLoading {
                            ProgressView()
                        } else {
                            Text("Save")
                        }
                    }
                    .disabled(!viewModel.canSave)
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
    
    static func factory(isPreview: Bool = false) -> EditTaskView {
        return EditTaskView(viewModel: EditTaskViewModelObservableObject())
    }

}

struct EditTaskView_Previews: PreviewProvider {
    static var previews: some View {
        EditTaskView.factory(isPreview: true)
    }
}
