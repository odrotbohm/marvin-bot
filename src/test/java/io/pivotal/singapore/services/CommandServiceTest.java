package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.repositories.CommandRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandServiceTest {

    @Mock private CommandRepository mockRepository;
    @InjectMocks private CommandService service = new CommandService();


    @Test
    public void getCommands() {
        List<Command> mockCommands = new ArrayList<Command>();
        mockCommands.add(new Command("", ""));
        when(mockRepository.findAll()).thenReturn(mockCommands);

        assertThat(service.getCommands().size(), is(1));
        verify(mockRepository).findAll();
    }

    @Test
    public void setCommands() {
        Command command = new Command("", "");
        service.addCommand(command);
        verify(mockRepository).save(command);
    }


}
