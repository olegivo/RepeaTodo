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

public extension MainNavigator {
    internal func asObservableObject() -> MainNavigatorObservableObject {
        return MainNavigatorObservableObject(wrapped: self)
    }
}

class MainNavigatorObservableObject: ObservableObject {
    private var wrapped: MainNavigator
    @Published
    var navigationDirection: NavigationDirection?
    
    init(wrapped: MainNavigator) {
        self.wrapped = wrapped
//        self.navigationDirection = wrapped.navigationDestination.value as! NavigationDirection?
        (wrapped.navigationDestination.asPublisher() as AnyPublisher<NavigationDestination?, Never>)
            .map({ navigationDestination in
                guard let navigationDestination = navigationDestination else { return nil }
                return NavigationDirection.forward(destination: navigationDestination, style: .present)
            })
            .receive(on: RunLoop.main)
            .assign(to: &$navigationDirection)
    }
}
