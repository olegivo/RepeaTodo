//
//  MainViewModel.swift
//  iosApp
//
//  Created by Олег Иващенко on 18.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

public extension MainViewModel {
    func asObservableObject() -> MainViewModelObservableObject {
        return MainViewModelObservableObject(wrapped: self)
    }
}

public class MainViewModelObservableObject : ObservableObject {
    
    private var wrapped: MainViewModel
    @Published private(set) var state: MainUiState
    
    init(wrapped: MainViewModel) {
        self.wrapped = wrapped
        state = wrapped.state.value as! MainUiState
        (wrapped.state.asPublisher() as AnyPublisher<MainUiState, Never>)
            .receive(on: RunLoop.main)
            .assign(to: &$state)
    }
    
    func onAddTaskClicked() {
        wrapped.onAddTaskClicked()
    }
    
    deinit {
        wrapped.onCleared()
    }
}
