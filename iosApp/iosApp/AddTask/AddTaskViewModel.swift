//
//  AddTaskViewModel.swift
//  iosApp
//
//  Created by Олег Иващенко on 18.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

public extension AddTaskViewModel {
    func asObservableObject() -> AddTaskViewModelObservableObject {
        return AddTaskViewModelObservableObject(wrapped: self)
    }
}

public class AddTaskViewModelObservableObject : ObservableObject {
    private var wrapped: AddTaskViewModel
    private var cancellables = Set<AnyCancellable>()
    
    @Published
    var navigationDirection: NavigationDirection?
    
    @Published
    var isLoading:Bool
    
    @Published
    var canAdd:Bool
    
    @Published
    var title: String {
        didSet {
            wrapped.title.setValue(title)
        }
    }
    
    init(wrapped: AddTaskViewModel) {
        self.wrapped = wrapped
        self.title = wrapped.title.value as! String
        self.isLoading = wrapped.isLoading.value as! Bool
        self.canAdd = wrapped.canAdd.value as! Bool

        (wrapped.title.asPublisher() as AnyPublisher<String, Never>)
            .receive(on: RunLoop.main)
            .assign(to: &$title)
        
        (wrapped.isLoading.asPublisher() as AnyPublisher<Bool, Never>)
            .receive(on: RunLoop.main)
            .assign(to: &$isLoading)
        
        (wrapped.canAdd.asPublisher() as AnyPublisher<Bool, Never>)
            .receive(on: RunLoop.main)
            .assign(to: &$canAdd)
        
        (wrapped.onAdded.asPublisher() as AnyPublisher<Void, Never>)
            .receive(on: RunLoop.main)
            .sink(receiveValue: { [weak self] _ in
                self?.navigationDirection = .back
            })
            .store(in: &cancellables)
    }
    
    func onAddClicked() {
        wrapped.onAddClicked()
    }
    
    func cancelTapped() {
        navigationDirection = .back
    }
    
    deinit {
        wrapped.onCleared()
    }
}
