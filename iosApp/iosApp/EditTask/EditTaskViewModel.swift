//
//  EditTaskViewModel.swift
//  iosApp
//
//  Created by Олег Иващенко on 21.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared


public extension EditTaskViewModel {
    func asObservableObject() -> EditTaskViewModelObservableObject {
        return EditTaskViewModelObservableObject(wrapped: self)
    }
}

public class EditTaskViewModelObservableObject : ObservableObject {
    
    private var wrapped: EditTaskViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published
    var title: String
    
    @Published
    var isSaving:Bool
    
    @Published
    var canSave:Bool
    
    @Published
    var navigationDirection: NavigationDirection?
    
    init(wrapped: EditTaskViewModel) {
        self.wrapped = wrapped
        self.title = wrapped.title.value as! String
        self.isSaving = wrapped.isSaving.value as! Bool
        self.canSave = wrapped.canSave.value as! Bool
        
        wrapped.title.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$title)
        
        wrapped.isSaving.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$isSaving)
        
        wrapped.canSave.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$canSave)
        
        wrapped.onSaved.asPublisher()
            .receive(on: RunLoop.main)
            .sink(receiveValue: { [weak self]  in
                self?.navigationDirection = .back
            })
            .store(in: &cancellables)
    }
    
    func onTitleChanged(_ title: String) {
        wrapped.title.setValue(title)
    }
    
    func onSaveClicked() {
        wrapped.onSaveClicked()
    }
    
    func onCancelClicked() {
        navigationDirection = .back
    }
}
