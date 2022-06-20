//
//  TasksListViewModel.swift
//  iosApp
//
//  Created by Олег Иващенко on 19.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

public extension TasksListViewModel {
    func asObservableObject() -> TasksListViewModelObservableObject {
        return TasksListViewModelObservableObject(wrapped: self)
    }
}

public class TasksListViewModelObservableObject : ObservableObject {
    private var wrapped: TasksListViewModel
    @Published private(set) var state: TasksListUiState
    
    @Published
    var navigationDirection: NavigationDirection?
    
    init(wrapped: TasksListViewModel) {
        self.wrapped = wrapped
        state = wrapped.state.value as! TasksListUiState
        (wrapped.state.asPublisher() as AnyPublisher<TasksListUiState, Never>)
            .receive(on: RunLoop.main)
            .assign(to: &$state)
    }
    
    func cancelTapped() {
        navigationDirection = .back
    }
    
    deinit {
        wrapped.onCleared()
    }
}
