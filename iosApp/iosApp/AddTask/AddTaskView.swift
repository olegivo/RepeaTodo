//
//  AddTaskView.swift
//  iosApp
//
//  Created by Олег Иващенко on 15.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct AddTaskView: View {
    
    @StateObject
    private var viewModel: AddTaskViewModelObservableObject
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    TextField("Enter A Title Here", text: $viewModel.title)
                }
                
                Section {
                    Button(action: {
                        viewModel.onAddClicked()
                    }) {
                        if viewModel.isLoading {
                            ProgressView()
                        } else {
                            Text("Add")
                        }
                    }
                    .disabled(!viewModel.canAdd)
                }
            }
            .navigationBarTitle("Add Todo", displayMode: .inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(action: {
                        viewModel.cancelTapped()
                    }) {
                        Image(systemName: "xmark")
                    }
                }
            }
            .handleNavigation($viewModel.navigationDirection)
        }
    }
    
    static func factory() -> AddTaskView {
        return AddTaskView(viewModel: AddTaskComponent().addTaskViewModel().asObservableObject())
    }
}

//extension AddTaskView {
//    class ViewModel: ObservableObject {
//        //        private var todoRepository = TodoRepository.shared
//        private var cancellables = Set<AnyCancellable>()
//        @Published
//        var navigationDirection: NavigationDirection?
//        @Published
//        var loading: Bool = false
//        
//        @State
//        var title: String = ""
//        
//        func onAddClicked() {
////            addTaskUseCase.invoke(task: Task(name: title))
//            loading = true
//            //            todoRepository.addTodo(title: title)
//            //                .sink { [unowned self] _ in
//            //                    navigationDirection = .back
//            //                }
//            //                .store(in: &cancellables)
//        }
//        
//        func cancelTapped() {
//            navigationDirection = .back
//        }
//    }
//}

//struct AddTaskView_Previews: PreviewProvider {
//    static var previews: some View {
//        AddTaskView()
//    }
//}
