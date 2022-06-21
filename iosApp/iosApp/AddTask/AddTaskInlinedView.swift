//
//  AddTaskInlinedView.swift
//  iosApp
//
//  Created by Олег Иващенко on 21.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AddTaskInlinedView: View {
    @StateObject
    private var viewModel: AddTaskViewModelObservableObject
    
    var body: some View {
        HStack {
            TextField("Enter A Title Here", text: $viewModel.title)
                .padding()
            Button(action: {
                viewModel.onAddClicked()
            }) {
                if viewModel.isLoading {
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
            .disabled(!viewModel.canAdd)
        }
    }
    
    
    static func factory(isPreview: Bool = false) -> AddTaskInlinedView {
        let viewModel = isPreview
                ? FakeAddTaskViewModel(initialState: AddTaskUiState(title: "", isLoading: false, isAdded: false))
                : AddTaskComponent().addTaskViewModel()
        return AddTaskInlinedView(viewModel: viewModel.asObservableObject())
    }
}

struct AddTaskInlinedView_Previews: PreviewProvider {
    static var previews: some View {
        AddTaskInlinedView.factory(isPreview: true)
    }
}
