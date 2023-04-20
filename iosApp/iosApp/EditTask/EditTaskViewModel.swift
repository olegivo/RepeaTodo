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
    
    let wrapped: EditTaskViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published
    var title: String
    
    @Published
    var isLoading:Bool
    
    @Published
    var isLoadingError:Bool
    
    @Published
    var canSave:Bool
    
    @Published
    var isSaving:Bool
    
    @Published
    var isSaveError:Bool
    
    init(wrapped: EditTaskViewModel) {
        self.wrapped = wrapped
        self.title = wrapped.title.value as! String
        self.isLoading = true // TODO: self.isLoading = wrapped.isLoading.value as! Bool
        self.isLoadingError = wrapped.isLoadingError.value as! Bool
        self.canSave = wrapped.canSave.value as! Bool
        self.isSaving = wrapped.isSaving.value as! Bool
        self.isSaveError = wrapped.isSaveError.value as! Bool
        
        wrapped.title.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$title)
        
        wrapped.isLoading.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$isLoading)
        
        wrapped.isLoadingError.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$isLoadingError)
        
        wrapped.isSaving.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$isSaving)
        
        wrapped.isSaveError.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$isSaveError)
        
        wrapped.canSave.asPublisher()
            .receive(on: RunLoop.main)
            .assign(to: &$canSave)        
    }
    
    func onTitleChanged(_ title: String) {
        wrapped.title.setValue(title)
    }
}
